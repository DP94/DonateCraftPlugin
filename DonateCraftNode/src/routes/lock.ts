import {getManager} from "typeorm";
import {Player} from "../entities/player";
import bodyParser from "body-parser";
import {RevivalLock} from "../entities/RevivalLock";

const express = require('express');
const router = express.Router();
const jsonParser = bodyParser.json();

// @ts-ignore
//GET to see if a lock exists or not
router.get('/:id', jsonParser, async (request, response, next) => {
    const key = request.params.id;
    if (key === undefined) {
        return;
    }
    const revivalRepository = getManager().getRepository(RevivalLock);
    let revivalLock: RevivalLock | undefined = await revivalRepository.findOne({key: key});
    if (revivalLock === undefined || revivalLock === null) {
        response.end(JSON.stringify(false));
    } else {
        response.end(JSON.stringify(true));
    }
    return;
});


//@ts-ignore
router.post('/', jsonParser, async (request, response) => {
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

module.exports = router;