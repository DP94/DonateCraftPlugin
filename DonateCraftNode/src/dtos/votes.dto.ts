import {Vote} from "../entities/vote";

export class VotesDto {
    votes!: Vote[];


    constructor(votes: Vote[]) {
        this.votes = votes;
    }
}