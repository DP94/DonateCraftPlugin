import {getManager} from "typeorm";
import {Player} from "../entities/player";
import bodyParser from "body-parser";
import {RevivalLock} from "../entities/RevivalLock";
import {DeathDto} from "../dtos/death.dto";
import {Death} from "../entities/death";
import {Request, Response} from "express";

const express = require('express');
const router = express.Router();
const jsonParser = bodyParser.json();

//GET to see if a lock exists or not
router.get('/:id', jsonParser, async (request: Request, response: Response) => {
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

router.post('/', jsonParser, async (request: Request, response: Response) => {
    const data: DeathDto = request.body.death;
    // Register key into DB
    try {
        try {
            console.log(`Death received for ${data.uuid}! Creating death data`);
            const playerRepository = getManager().getRepository(Player);
            let player: Player | undefined = await playerRepository.findOne({uuid: data.uuid})
            if (player === undefined) {
                player = new Player();
                player.uuid = data.uuid;
            }
            player.name = data.name;
            const death = new Death();
            death.reason = data.reason;
            death.date = new Date();
            death.player = player;
            player.deaths?.push(death);
            await getManager().save(player);
            console.log(`Successfully recorded death for ${data.uuid}`);
            response.sendStatus(200);
        } catch (e) {
            console.log(`Encountered issue when trying to persist user stats! ${e}`);
        }

        const lockRepository = getManager().getRepository(RevivalLock);
        let lock: RevivalLock | undefined = await lockRepository.findOne({key: data.uuid})
        if (lock === undefined) {
            console.log(`Creating a new lock for ${data.uuid}`);
            lock = new RevivalLock();
            lock.key = data.uuid;
            lock.unlocked = false;
            await getManager().save(lock);
            console.log(`Successfully created lock for ${data.uuid}`);
        } else {
            response.status(400).send('Lock already exists')
        }
    } catch (e) {
        console.log(e);
    }
});

module.exports = router;