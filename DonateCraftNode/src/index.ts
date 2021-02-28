import express from 'express';
import {createConnection, getManager} from 'typeorm';
import {Player} from './entities/player';
import {RevivalLock} from './entities/RevivalLock';
import bodyParser from 'body-parser';
import cors from 'cors';
import {PlayersDto} from "./dtos/players.dto";
import path from "path";
import http from 'http';
import {RevivalsDto} from "./dtos/revivals.dto";
import {Donation} from "./entities/donation";

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
const app = express();
const PORT = 8000;
const jsonParser = bodyParser.json();

connectToDB().then(async () => {
    app.use(cors());
    app.use(express.static(process.cwd() + "/build/public/"));
    app.listen(PORT, () => {
        console.log(`Server is running at http://localhost:${PORT}`);
    });

    app.get('/', (request, response) => {
        response.sendFile(path.resolve(__dirname, 'build', 'index.html'));
    });

    app.get('/players', jsonParser, async (request, response) => {
        try {
            const deathRepository = getManager().getRepository(Player);
            const players: Player[] = await deathRepository.find({
                order: {
                    deathcount: "DESC",
                    lastdeathreason: "DESC"
                }
            });
            const playersDTO: PlayersDto = new PlayersDto();
            playersDTO.players = players;
            response.setHeader('Content-Type', 'application/json');
            response.end(JSON.stringify(playersDTO));
        } catch (e) {
            console.log(e);
        }
    });
    // Revival API
    app.post('/lock', jsonParser, async (request, response) => {
        const data: Player = request.body.death;
        // Register key into DB
        try {
            try {
                const deathRepository = getManager().getRepository(Player);
                let death: Player | undefined = await deathRepository.findOne({uuid: data.uuid})
                if (death === undefined) {
                    death = new Player();
                    death.uuid = data.uuid;
                }
                death.name = data.name;
                death.lastdeathreason = data.lastdeathreason;
                death.deathcount++;
                await getManager().save(death);
            } catch (e) {
                console.log('Encountered issue when trying to persist user stats!');
                console.log(e);
            }

            const lockRepository = getManager().getRepository(RevivalLock);
            let lock: RevivalLock | undefined = await lockRepository.findOne({key: data.uuid})
            if (lock === undefined) {
                lock = new RevivalLock();
                lock.key = data.uuid;
                lock.unlockurl = '';
                lock.unlocked = false;
                await getManager().save(lock);
                response.send('test');
            } else {
                response.status(400).send('Lock already exists')
            }
        } catch (e) {
            console.log(e);
        }
    });

    app.get('/unlockURL/:key', jsonParser, async (request, response) => {
        const key = request.params.key;
        try {
            const lockRepository = getManager().getRepository(RevivalLock);
            let lock: RevivalLock | undefined = await lockRepository.findOne({key: key});
            if (lock === undefined) {
                response.status(404).send('Lock not found for key: ' + key);
            } else {
                response.send(lock.unlockurl);
            }
        } catch (e) {
            console.log(e);
        }
    });

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
        const uuid = request.body.uuid;
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
        const donationData = await util.promisify(xmlToJson)("http://api.staging.justgiving.com/" + DC_JG_API_KEY + "/v1/donation/" + donationid);
        if (donationData && donationData.donation && donationData.donation.status && donationData.donation.status.length > 0) {
            try {
                // If data is returned and we have a status
                if (data && donationData.donation.status && donationData.donation.status.length > 0) {
                    const status = donationData.donation.status[0];
                    // We can also unlock now that the donation is accepted and hits the minimum value to avoid future calls.
                    if (status === "Accepted" || status === "Pending") {
                        const reference = donationData.donation.thirdPartyReference;
                        const lockRepository = getManager().getRepository(RevivalLock);
                        let lock: RevivalLock | undefined = await lockRepository.findOne({key: key})
                        if (lock === undefined) {
                            // We have a donation that we can't reference.
                            response.status(404).send("Cannot find associated lock for donation: " + donationid + " ref: " + reference);
                            return;
                        } else if (!lock.unlocked) {
                            const donationRepository = getManager().getRepository(Donation);
                            const donation = new Donation();
                            donation.donationId = parseInt(donationid);
                            donation.amount = donationData.donation.amount;
                            donation.charity = donationData.donation.charityId;
                            donation.uuid = key;
                            donation.date = new Date();

                            await donationRepository.save(donation);
                            lock.donation = donation;
                            lock.unlocked = true;
                            await getManager().save(lock);
                        }
                        response.redirect('/#/?status=success');
                    } else if (status === "Failed") {
                        response.sendStatus(500);
                        response.redirect('/#/?status=fail');
                    }
                }
            } catch (e) {
                console.log(e);
            }
        }
    })
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
        logging: false
    });
}