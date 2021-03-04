import express from 'express';
import {createConnection, getManager} from 'typeorm';
import {Player} from './entities/player';
import {RevivalLock} from './entities/RevivalLock';
import bodyParser from 'body-parser';
import cors from 'cors';
import http from 'http';
import {RevivalsDto} from "./dtos/revivals.dto";
import {Donation} from "./entities/donation";

const util = require('util')
require('dotenv').config()

const DC_DB_USERNAME = process.env.DC_DB_USERNAME;
const DC_DB_PASSWORD = process.env.DC_DB_PASSWORD;
const DC_JG_API_KEY = process.env.DC_JG_API_KEY;
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

const index = require('./routes/index.route');
const players = require('./routes/players');
const lock = require('./routes/lock')

const app = express();
const PORT = 8000;
const jsonParser = bodyParser.json();
app.use(jsonParser);
app.use('/', index);
app.use('/players', players);
app.use('/lock', lock);
app.use(cors());
app.use(express.static(process.cwd() + "/build/public/"));

app.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
});

connectToDB().then(async () => {
    app.get('/unlocked', jsonParser, async (request, response) => {
        try {
            const lockRepository = getManager().getRepository(RevivalLock);
            let lock: RevivalLock[] | undefined = await lockRepository.find({unlocked: true});
            const revivals: RevivalsDto = new RevivalsDto();
            revivals.revivals = lock;
            response.setHeader('Content-Type', 'application/json');
            response.end(JSON.stringify(revivals));
        } catch (e) {
            console.log(e);
        }
    });

    app.post('/revived', jsonParser, async (request, response) => {
        const uuid = request.body.revival.key;
        try {
            const lockRepository = getManager().getRepository(RevivalLock);
            let lock: RevivalLock | undefined = await lockRepository.findOne({key: uuid});
            if (lock === undefined) {
                response.status(404).send("Player lock not found!");
            } else if (lock.unlocked) {
                await lockRepository.remove(lock);
            }
        } catch (e) {
            console.log(e);
        }
    });

    app.get('/callback', jsonParser, async (request, response) => {
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
        const donationData = await util.promisify(fireGetJSONRequest)(`api.staging.justgiving.com`, `/${DC_JG_API_KEY}/v1/donation/${donationid}`);
        if (donationData && donationData.status && donationData.status.length > 0) {
            try {
                // If data is returned and we have a status
                if (data && donationData.status && donationData.status.length > 0) {
                    const status = donationData.status;
                    // We can also unlock now that the donation is accepted and hits the minimum value to avoid future calls.
                    if (status === "Accepted" || status === "Pending") {
                        const lockRepository = getManager().getRepository(RevivalLock);
                        let lock: RevivalLock | undefined = await lockRepository.findOne({key: key})
                        if (lock === undefined) {
                            //In the event of someone donating when no lock is present
                            response.redirect('/#/?status=success');
                            return;
                        } else if (!lock.unlocked) {
                            const donationRepository = getManager().getRepository(Donation);
                            const donation = new Donation();
                            donation.donationId = parseInt(donationid);
                            donation.amount = donationData.amount;
                            donation.charity = donationData.charityId;
                            donation.uuid = key;

                            const charityData = await util.promisify(fireGetJSONRequest)('api.staging.justgiving.com', `/${DC_JG_API_KEY}/v1/charity/${donation.charity}`);
                            donation.charityName = charityData.name;
                            donation.date = new Date();

                            await donationRepository.save(donation);
                            lock.donation = donation;
                            lock.unlocked = true;
                            await getManager().save(lock);
                            response.redirect('/#/?status=success');
                            return;
                        } else {
                            response.redirect('/#/?status=success');
                            return;
                        }
                    } else if (status === "Failed" || status === "Cancelled") {
                        response.redirect(`/#/?status=error&key=${key}`);
                        return;
                    }
                }
            } catch (e) {
                console.log(e);
            }
        }
    })
});

function fireGetJSONRequest(host: string, path: string, callback: Function) {
    const options = {
        host: host,
        path: path,
        headers: {'Content-Type': 'application/json'},
        method: 'GET',
    }
    console.log(`Firing request to ${host}${path}`);
    http.get(options, (res: http.IncomingMessage) => {
        let json = '';

        res.on('data', function (chunk: string) {
            json += chunk;
        });

        res.on('error', function (e: string) {
            callback(e, null);
        });

        res.on('timeout', function (e: string) {
            callback(e, null);
        });

        res.on('end', function () {
            json = JSON.parse(json);
            callback(null, json);
        });
    });
}

function connectToDB() {
    return createConnection({
        type: "mysql",
        host: "localhost",
        port: 3306,
        username: DC_DB_USERNAME,
        password: DC_DB_PASSWORD,
        database: "donatecraft",
        entities: [
            Player, Donation, RevivalLock
        ],
        synchronize: true,
        logging: false,
        bigNumberStrings: false,
    });
}

module.exports = app;