import {getManager} from "typeorm";
import bodyParser from "body-parser";
import {NextFunction, Request, Response} from "express";
import {Vote} from "../entities/vote";
import {VotesDto} from "../dtos/votes.dto";

const express = require('express');
const router = express.Router();
const jsonParser = bodyParser.json();

router.post('/', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    try {
        console.log(`Received a request to persist a vote!`);
        const vote: Vote = request.body.vote;
        console.log(`Vote name: ${vote.question}`);
        const voteRepository = getManager().getRepository(Vote);
        await voteRepository.save(vote);
        console.log(`Successfully saved vote!`)
        response.status(200);
    } catch (e) {
        console.log(`Error when trying to save vote: ${e}`);
        response.status(500).end();
    }
})

router.get('/', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    try {
        console.log(`Received a request to retrieve all votes!`);
        const voteRepository = getManager().getRepository(Vote);
        const votes: Vote[] = await voteRepository.find();
        const votesDTO: VotesDto = new VotesDto(votes);
        response.setHeader('Content-Type', 'application/json');
        response.end(JSON.stringify(votesDTO));
        response.status(200);
    } catch (e) {
        console.log(`Error when trying to retrieve all votes: ${e}`);
        response.status(500).end();
    }
})

router.get('/:id', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    try {
        const voteId: any = request.params.id;
        console.log(`Received a request to retrieve a vote with id ${voteId}!`);
        const voteRepository = getManager().getRepository(Vote);
        const vote: Vote | undefined = await voteRepository.findOne({id: voteId});
        if (vote === undefined) {
            console.log(`Could not find a vote with an ID of ${voteId}!`);
            response.status(404).end();
            return;
        }
        response.setHeader('Content-Type', 'application/json');
        response.end(JSON.stringify(vote));
        response.status(200);
    } catch (e) {
        console.log(`Error when trying to retrieve a vote ${e}!`);
        response.status(500).end();
    }
})

module.exports = router;