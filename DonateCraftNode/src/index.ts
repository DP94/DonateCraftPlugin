import express from 'express';
import {createConnection} from 'typeorm';
import {Death} from './entities/Death';
import {RevivalLock} from './entities/RevivalLock';
import bodyParser from 'body-parser';
import cors from 'cors';
import {DeathsDto} from "./dtos/deaths.dto";
import path from "path";
import http from 'http';
import {RevivalsDto} from "./dtos/revivals.dto";

const {v4: uuidv4} = require('uuid');
const parseString = require('xml2js').parseString;

const util = require('util')
require('dotenv').config()

const DC_DB_USERNAME = process.env.DC_DB_USERNAME;
const DC_DB_PASSWORD = process.env.DC_DB_PASSWORD;
const DC_JG_API_KEY = process.env.DC_JG_API_KEY;
const DC_EXTERNAL_HOST = process.env.DC_EXTERNAL_HOST;
if (!DC_DB_USERNAME) {
    console.error("DC_DB_USERNAME not set");
    process.exit(-1);
}
if (!DC_DB_PASSWORD) {
    console.error("DC_DB_PASSWORD not set");
    process.exit(-1);
}
if (!DC_JG_API_KEY) {
    console.error("DC_JG_API_KEY not set");
    process.exit(-1);
}
if (!DC_EXTERNAL_HOST) {
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

app.get('/deaths', jsonParser, (request, response) => {
    connectToDB().then(async connection => {
        try {
            const deathRepository = connection.getRepository(Death);
            const deaths: Death[] = await deathRepository.find({order: {deathcount: "DESC", lastdeathreason: "DESC"}});
            const deathDTO: DeathsDto = new DeathsDto();
            deathDTO.deaths = deaths;
            response.setHeader('Content-Type', 'application/json');
            response.end(JSON.stringify(deathDTO));
        } catch (e) {
            console.log(e);
        } finally {
            await connection.close();
        }
    }).catch(error => console.log(error));
});

function connectToDB() {
    return createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            Death, RevivalLock
        ],
        synchronize: true,
        logging: false
    });
}

// Revival API
app.post('/lock', jsonParser, (request, response) => {
    const data: Death = request.body.death;
    // Register key into DB
    connectToDB().then(async connection => {
        try {
            try {
                const deathRepository = connection.getRepository(Death);
                let death: Death | undefined = await deathRepository.findOne({uuid: data.uuid})
                if (death === undefined) {
                    death = new Death();
                    death.uuid = data.uuid;
                }
                death.name = data.name;
                death.lastdeathreason = data.lastdeathreason;
                death.deathcount++;
                await connection.manager.save(death);
            } catch (e) {
                console.log('Encountered issue when trying to persist user stats!');
                console.log(e);
            }

            const lockRepository = connection.getRepository(RevivalLock);
            let lock: RevivalLock | undefined = await lockRepository.findOne({key: data.uuid})
            if (lock === undefined) {
                lock = new RevivalLock();
                lock.key = data.uuid;
                lock.unlockurl = '';
                lock.donationid = -1;
                lock.unlocked = false;
                await connection.manager.save(lock);
                response.send('test');
            } else {
                response.status(400).send('Lock already exists')
            }
        } catch (e) {
            console.log(e);
        } finally {
            await connection.close();
        }
    }).catch(error => console.log(error));
});

app.get('/unlockURL/:key', jsonParser, (request, response) => {
    const key = request.params.key;
    connectToDB().then(async connection => {
        try {
            const lockRepository = connection.getRepository(RevivalLock);
            let lock: RevivalLock | undefined = await lockRepository.findOne({key: key});
            if (lock === undefined) {
                response.status(404).send('Lock not found for key: ' + key);
            } else {
                response.send(lock.unlockurl);
            }
        } catch (e) {
            console.log(e);
        } finally {
            await connection.close();
        }
    }).catch(error => console.log(error));
});

function xmlToJson(url: string, callback: Function) {
    const req = http.get(url, (res: http.IncomingMessage) => {
        let xml = '';

        res.on('data', function (chunk: string) {
            xml += chunk;
        });

        res.on('error', function (e: string) {
            callback(e, null);
        });

        res.on('timeout', function (e: string) {
            callback(e, null);
        });

        res.on('end', function () {
            parseString(xml, function (err: string, result: string) {
                callback(null, result);
            });
        });
    });
}

app.get('/unlocked', jsonParser, (request, response) => {
    connectToDB().then(async connection => {
        try {
            const lockRepository = connection.getRepository(RevivalLock);
            let lock: RevivalLock[] | undefined = await lockRepository.find({unlocked: true});
            const revivals: RevivalsDto = new RevivalsDto();
            revivals.revivals = lock;
            response.setHeader('Content-Type', 'application/json');
            response.end(JSON.stringify(revivals));
        } catch (e) {
            console.log(e);
        } finally {
            await connection.close();
        }
    }).catch(error => console.log(error));
});

app.post('/revived', jsonParser, (request, response) => {
    const uuid = request.body.uuid;
    connectToDB().then(async connection => {
        try {
            const lockRepository = connection.getRepository(RevivalLock);
            let lock: RevivalLock | undefined = await lockRepository.findOne({key: uuid});
            if (lock === undefined) {
                response.status(404).send("Player lock not found!");
            } else {
                await lockRepository.remove(lock);
            }
        } catch (e) {
            console.log(e);
        } finally {
            await connection.close();
        }
    }).catch(error => console.log(error));
});

app.get('/callback', jsonParser, (request, response) => {
    const data = request.query.data as string;
    if (data === undefined || !data.includes("|")) {
        response.status(404).send("Data from callback is not correct: " + data);
        return;
    }
    const dataSplit = data.split("|");
    if (dataSplit.length < 2) {
        response.status(404).send("Not enough data found in callback: " + dataSplit);
        return;
    }
    const donationid = dataSplit[0];
    const key = dataSplit[1];
    if (!donationid) {
        response.status(404).send("No donation id found");
        return;
    }

    connectToDB().then(async connection => {
        try {
            const donationData = await util.promisify(xmlToJson)("http://api.staging.justgiving.com/" + DC_JG_API_KEY + "/v1/donation/" + donationid);
            console.debug(JSON.stringify(donationData, null, 2));

            // If data is returned and we have a status
            if (donationData && donationData.donation && donationData.donation.status && donationData.donation.status.length > 0) {
                const reference = donationData.donation.thirdPartyReference;
                const lockRepository = connection.getRepository(RevivalLock);
                let lock: RevivalLock | undefined = await lockRepository.findOne({key: key})
                if (lock === undefined) {
                    // We have a donation that we can't reference.
                    response.status(404).send("Cannot find associated lock for donation: " + donationid + " ref: " + reference);
                    return;
                } else {
                    lock.donationid = parseInt(donationid);
                }

                const status = donationData.donation.status[0];
                lock.unlocked = true;
                await connection.manager.save(lock);
                response.redirect('/');
            }
        } catch (e) {
            console.log(e);
        } finally {
            await connection.close();
        }
    }).catch(error => console.log(error));
});
