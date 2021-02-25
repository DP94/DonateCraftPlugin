import express from 'express';
import {createConnection} from 'typeorm';
import {Death} from './entities/Death';
import {RevivalLock} from './entities/RevivalLock';
import bodyParser from 'body-parser';
import cors from 'cors';
import {DeathsDto} from "./dtos/deaths.dto";
import path from "path";
const { v4: uuidv4 } = require('uuid');
const parseString = require('xml2js').parseString;
import http from 'http';
const util = require('util')
require('dotenv').config()

const DC_DB_USERNAME = process.env.DC_DB_USERNAME;
const DC_DB_PASSWORD = process.env.DC_DB_PASSWORD;
const DC_JG_API_KEY = process.env.DC_JG_API_KEY;
const DC_EXTERNAL_HOST = process.env.DC_EXTERNAL_HOST;
if(!DC_DB_USERNAME) {
  console.error("DC_DB_USERNAME not set");
  process.exit(-1);
}
if(!DC_DB_PASSWORD) {
  console.error("DC_DB_PASSWORD not set");
  process.exit(-1);
}
if(!DC_JG_API_KEY) {
  console.error("DC_JG_API_KEY not set");
  process.exit(-1);
}
if(!DC_EXTERNAL_HOST) {
  console.error("DC_EXTERNAL_HOST not set");
  process.exit(-1);
}

const app = express();
const PORT = 8000;
const jsonParser = bodyParser.json();
app.use(cors());
app.use(express.static(process.cwd() + "/build/public/"));
app.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
});

app.get('/', (request, response) => {
    response.sendFile(path.resolve(__dirname, 'build', 'index.html'));
});

// Death API

app.post('/death', jsonParser, (request, res) => {
    const uuid = request.body.uuid;
    const name = request.body.name;
    createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            Death
        ],
        synchronize: true,
        logging: false
    }).then(async connection => {
        const deathRepository = connection.getRepository(Death);
        let death: Death | undefined = await deathRepository.findOne({uuid: uuid})
        if (death === undefined) {
            death = new Death();
            death.uuid = uuid;
            death.deathcount = 1;
            death.name = name;
        } else {
            death.name = name;
            death.deathcount++;
        }
        await connection.manager.save(death);
        await connection.close();
    }).catch(error => console.log(error));
});

app.get('/deaths', jsonParser, (request, response) => {
    createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            Death
        ],
        synchronize: true,
        logging: false
    }).then(async connection => {
        const deathRepository = connection.getRepository(Death);
        const deaths: Death[] = await deathRepository.find();
        const deathDTO: DeathsDto = new DeathsDto();
        deathDTO.deaths = deaths;
        await connection.close();
        response.setHeader('Content-Type', 'application/json');
        response.end(JSON.stringify(deathDTO));
    }).catch(error => console.log(error));
});

// Revival API

app.post('/lock', jsonParser, (request, response) => {
  const key = request.body.key;
  const charityId = request.body.charityId || 13441;
  const value = request.body.donationValue || 2;
  const currency = request.body.donationCurrency || "GBP";
  const skipGiftAid = request.body.skipGiftAid || false;

  // References can only be 8 characters. This should be sufficient as each
  // donation id is unique and is used on the callback.
  const reference = uuidv4().substring(0, 8);

  const callbackURL = encodeURIComponent(DC_EXTERNAL_HOST + "/callback?jgDonationId=JUSTGIVING-DONATION-ID");

  const url = "https://link.staging.justgiving.com/v1/charity/donate/charityId/" + charityId
  + "?donationValue=" + value
  + "&currency=" + currency
  + "&reference=" + reference
  + "&skipGiftAid=" + skipGiftAid
  + "&exiturl=" + callbackURL;

  // Register key into DB
  createConnection({
      type: "mysql",
      host: "localhost",
      port: 3306,
      username: DC_DB_USERNAME,
      password: DC_DB_PASSWORD,
      database: "donatecraft",
      entities: [
          RevivalLock
      ],
      synchronize: true,
      logging: false
  }).then(async connection => {
      const lockRepository = connection.getRepository(RevivalLock);
      let lock: RevivalLock | undefined = await lockRepository.findOne({key: key})
      if (lock === undefined) {
          lock = new RevivalLock();
          lock.key = key;
          lock.reference = reference;
          lock.unlockurl = url;
          lock.donationid = -1;
          lock.unlocked = false;
          lock.value = value;
          await connection.manager.save(lock);
          await connection.close();
          response.send(url);
      } else {
          await connection.close();
          response.status(400).send('Lock already exists')
      }
  }).catch(error => console.log(error));
});

app.get('/unlockURL/:key', jsonParser, (request, response) => {
    const key = request.params.key;
    createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            RevivalLock
        ],
        synchronize: true,
        logging: false
    }).then(async connection => {
        const lockRepository = connection.getRepository(RevivalLock);
        let lock: RevivalLock | undefined = await lockRepository.findOne({key: key});
        await connection.close();
        if (lock === undefined) {
            response.status(404).send('Lock not found for key: ' + key);
        } else {
            response.send(lock.unlockurl);
        }
    }).catch(error => console.log(error));
});

function xmlToJson(url : string, callback : Function) {
  const req = http.get(url, (res : http.IncomingMessage) => {
    let xml = '';

    res.on('data', function(chunk : string) {
      xml += chunk;
    });

    res.on('error', function(e : string) {
      callback(e, null);
    });

    res.on('timeout', function(e : string) {
      callback(e, null);
    });

    res.on('end', function() {
      parseString(xml, function(err : string, result : string) {
        callback(null, result);
      });
    });
  });
}

app.get('/unlocked/:key', jsonParser, (request, response) => {
  const key = request.params.key;
  createConnection({
      type: "mysql",
      host: "localhost",
      port: 3306,
      username: DC_DB_USERNAME,
      password: DC_DB_PASSWORD,
      database: "donatecraft",
      entities: [
          RevivalLock
      ],
      synchronize: true,
      logging: false
  }).then(async connection => {
      const lockRepository = connection.getRepository(RevivalLock);
      let lock: RevivalLock | undefined = await lockRepository.findOne({key: key})
      if (lock === undefined) {
          response.status(404).send('Lock not found for key: ' + key);
      } else {
          // If already unlocked quick return.
          if (lock.unlocked) {
            await connection.close();
            response.send(lock.unlocked);
            return;
          }

          // Otherwise if the donation id is set check the JG API status
          if (lock.donationid > 0) {
            const data = await util.promisify(xmlToJson)("http://api.staging.justgiving.com/"+DC_JG_API_KEY+"/v1/donation/" + lock.donationid);
            console.debug(JSON.stringify(data, null, 2));

            // If data is returned and we have a status
            if (data && data.donation.status && data.donation.status.length > 0) {
                const status = data.donation.status[0];
                // We can also unlock now that the donation is accepted and hits the minimum value to avoid future calls.
                if (status === "Accepted") {
                  if (data.donation.amount[0] >= lock.value) {
                    lock.unlocked = true;
                    await connection.manager.save(lock);
                    await connection.close();
                    response.send(true);
                  } else {
                    console.error("User changed value at checkout to under required threshold. Deadlock.");
                    await connection.close()
                    response.send(false);
                  }
                }
            }
          }

          // Otherwise return false; we have yet to get a callback to identify donation id.
          await connection.close();
          response.send(false);
      }
  }).catch(error => console.log(error));
});

app.get('/callback', jsonParser, (request, response) => {
  const donationid = request.query.jgDonationId as string;
  if (!donationid) {
    response.status(404).send("No donation id found");
    return;
  }

  createConnection({
      type: "mysql",
      host: "localhost",
      port: 3306,
      username: DC_DB_USERNAME,
      password: DC_DB_PASSWORD,
      database: "donatecraft",
      entities: [
          RevivalLock
      ],
      synchronize: true,
      logging: false
  }).then(async connection => {

    const data = await util.promisify(xmlToJson)("http://api.staging.justgiving.com/"+DC_JG_API_KEY+"/v1/donation/" + donationid);
    console.debug(JSON.stringify(data, null, 2));

    // If data is returned and we have a status
    if (data && data.donation && data.donation.status && data.donation.status.length > 0) {
        const reference = data.donation.thirdPartyReference;
        const lockRepository = connection.getRepository(RevivalLock);
        let lock: RevivalLock | undefined = await lockRepository.findOne({reference: reference})
        if (lock === undefined) {
          // We have a donation that we can't reference.
          await connection.close();
          response.status(404).send("Cannot find associated lock for donation: " + donationid + " ref: " + reference);
          return;
        } else {
          lock.donationid = parseInt(donationid);
        }

        const status = data.donation.status[0];
        // We can also unlock now that the donation is accepted and hits the minimum value to avoid future calls.
        if (status === "Accepted") {
          if (data.donation.amount[0] >= lock.value) {
            lock.unlocked = true;
          } else {
            console.error("User changed value at checkout to under required threshold. Deadlock.");
          }
        }

        await connection.manager.save(lock);
        await connection.close();
        response.send("Donation callback received successfully. Thank you!");
    }
  }).catch(error => console.log(error));
});

