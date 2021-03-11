import {Column, Entity, ManyToOne, PrimaryColumn} from "typeorm";
import {Player} from "./player";
import {Session} from "./session";

@Entity()
export class Donation {

    @PrimaryColumn()
    id!: number;

    @Column({type: "decimal", precision: 15, scale: 2, nullable: true})
    amount!: number;

    @ManyToOne(() => Player, player => player.donations)
    player!: Player;

    @ManyToOne(() => Player, player => player.donations)
    paidForBy?: Player;

    @ManyToOne(() => Session, session => session.donations)
    session!: Session;

    @Column()
    date!: Date;

    @Column()
    charity!: number;

    @Column()
        //Here so Minecraft plugin can show this without a second request
    charityName?: string;
}
