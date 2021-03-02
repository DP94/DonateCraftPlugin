import {Column, Entity, ManyToOne, PrimaryGeneratedColumn} from "typeorm";
import {Player} from "./player";

@Entity()
export class Donation {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    donationId!: number;

    @Column({type: "decimal", precision: 15, scale: 2, nullable: true})
    amount!: number;

    @ManyToOne(() => Player, player => player.donations)
    uuid!: string;

    @Column()
    date!: Date;

    @Column()
    charity!: number;

    @Column()
        //Here so Minecraft plugin can show this without a second request
    charityName?: string;
}
