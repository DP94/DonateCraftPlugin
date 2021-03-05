import {getManager} from "typeorm";
import {Player} from "../entities/player";
import {PlayersDto} from "../dtos/players.dto";
import bodyParser from "body-parser";
import {NextFunction, Request, Response} from "express";

const express = require('express');
const router = express.Router();
const jsonParser = bodyParser.json();

router.get('/', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    try {
        const playerRepository = getManager().getRepository(Player);

        let ids: Player[] = await playerRepository.createQueryBuilder('player').leftJoinAndSelect('player.deaths', 'deaths').
        select('player.uuid').addSelect('COUNT(*)', 'count').groupBy('player.uuid')
            .orderBy('count', 'DESC').getMany();

        const players: Player[] = await playerRepository.createQueryBuilder('player')
            .leftJoinAndSelect('player.deaths', 'deaths')
            .leftJoinAndSelect('player.donations', 'donations')
            .andWhereInIds(ids).orderBy(getPlayerIdsSortedString(ids)).getMany();
        const playersDTO: PlayersDto = new PlayersDto();
        playersDTO.players = players;
        response.setHeader('Content-Type', 'application/json');
        response.end(JSON.stringify(playersDTO));
    } catch (e) {
        console.log(e);
    }
});

function getPlayerIdsSortedString(ids: Player[]) {
    let value = 'FIELD(player.uuid,'
    for (let i = 0; i < ids.length; i++) {
        value += `'${ids[i].uuid}'`;
        if (i !== ids.length - 1) {
            value += ',';
        }
    }
    value += ')'
    return value;
}

router.get('/:id', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    const id = request.params.id;
    const deathRepository = getManager().getRepository(Player);
    const player: Player | undefined = await deathRepository.findOne({uuid: id});
    response.setHeader('Content-Type', 'application/json');
    response.end(JSON.stringify(player));
});

module.exports = router;