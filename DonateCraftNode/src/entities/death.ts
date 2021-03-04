import {Column, Entity, ManyToOne, PrimaryGeneratedColumn} from "typeorm";
import {Player} from "./player";

@Entity()
export class Death {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    reason!: string;

    @Column()
    date!: Date;

    @ManyToOne(() => Player, player => player.deaths)
    player!: Player;
}