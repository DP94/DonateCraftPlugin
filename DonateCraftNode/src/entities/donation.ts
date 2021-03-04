import {Column, Entity, ManyToOne, PrimaryColumn} from "typeorm";
import {Player} from "./player";

@Entity()
export class Donation {

    @PrimaryColumn()
    id!: number;

    @Column({type: "decimal", precision: 15, scale: 2, nullable: true})
    amount!: number;

    @ManyToOne(() => Player, player => player.donations)
    player!: Player;

    @Column()
    date!: Date;

    @Column()
    charity!: number;

    @Column()
        //Here so Minecraft plugin can show this without a second request
    charityName?: string;
}
