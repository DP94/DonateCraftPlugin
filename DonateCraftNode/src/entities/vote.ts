import {Column, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn} from "typeorm";
import {Player} from "./player";
import {VoteRecord} from "./vote.record";

@Entity()
export class Vote {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    question!: string;

    @ManyToOne(() => Player)
    author!: Player;

    @Column()
    dateCalled!: Date;

    @Column()
    dateFinished?: Date;

    @OneToMany(() => VoteRecord, voteRecord => voteRecord.vote, {
        cascade: true,
        eager: true
    })
    voteRecords!: VoteRecord[];
}