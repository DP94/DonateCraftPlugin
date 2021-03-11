import {Column, Entity, ManyToOne, PrimaryGeneratedColumn} from "typeorm";
import {Player} from "./player";
import {Vote} from "./vote";

@Entity()
export class VoteRecord {

    @PrimaryGeneratedColumn()
    id!: number;

    @ManyToOne(() => Vote, vote => vote.voteRecords)
    vote!: Vote;

    @ManyToOne(() => Player)
    voter!: Player;

    @Column()
    decision!: string;

    @Column()
    timeVoted!: Date;
}