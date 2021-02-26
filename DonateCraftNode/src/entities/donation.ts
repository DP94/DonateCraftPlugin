import {Column, Entity, ManyToOne, PrimaryGeneratedColumn} from "typeorm";
import {Player} from "./player";

@Entity()
export class Donation {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    donationId!: number;

    @Column()
    amount!: number;

    @ManyToOne(() => Player, player => player.donations)
    uuid!: string;

    @Column()
    date!: Date;

    @Column()
    charity!: number;
}
