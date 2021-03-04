import {getManager} from "typeorm";
import {Player} from "../entities/player";
import {PlayersDto} from "../dtos/players.dto";
import bodyParser from "body-parser";
import {NextFunction, Request, Response} from "express";

const express = require('express');
const router = express.Router();
const jsonParser = bodyParser.json();

// @ts-ignore
router.get('/', jsonParser, async (request, response, next) => {
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

router.get('/:id', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    const id = request.params.id;
    const deathRepository = getManager().getRepository(Player);
    const player: Player | undefined = await deathRepository.findOne({uuid: id});
    response.setHeader('Content-Type', 'application/json');
    response.end(JSON.stringify(player));
});

module.exports = router;