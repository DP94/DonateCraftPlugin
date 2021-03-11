import {getManager} from "typeorm";
import bodyParser from "body-parser";
import {NextFunction, Request, Response} from "express";
import {Session} from "../entities/session";
import {SessionDto} from "../dtos/session.dto";

const express = require('express');
const router = express.Router();
const jsonParser = bodyParser.json();

router.get('/', jsonParser, async (request: Request, response: Response) => {
    const sessionRepository = getManager().getRepository(Session);
    let sessions: Session[] | undefined = await sessionRepository.find();
    const sessionsDto = new SessionDto();
    sessionsDto.sessions = sessions;
    response.end(JSON.stringify(sessionsDto));
});

router.get('/:id', jsonParser, async (request: Request, response: Response, next: NextFunction) => {
    try {
        const sessionId: any = request.params.id;
        console.log(`Received a request to retrieve a session with id ${sessionId}!`);
        const sessionRepository = getManager().getRepository(Session);
        const session: Session | undefined = await sessionRepository.findOne({id: sessionId});
        if (session === undefined) {
            console.log(`Could not find a session with an ID of ${sessionId}!`);
            response.status(404).end();
            return;
        }
        response.setHeader('Content-Type', 'application/json');
        response.end(JSON.stringify(session));
        response.status(200);
    } catch (e) {
        console.log(`Error when trying to retrieve a session ${e}!`);
        response.status(500).end();
    }
})

module.exports = router;