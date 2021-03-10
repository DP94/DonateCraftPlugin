import {RevivalLock} from "../entities/RevivalLock";

export class RevivalsDto {
    revivals!: RevivalLock[];


    constructor(revivals: RevivalLock[]) {
        this.revivals = revivals;
    }
}
