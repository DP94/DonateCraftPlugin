import {Column, Entity, OneToMany, PrimaryColumn} from 'typeorm';
import {Donation} from "./donation";

@Entity()
export class Player {

    @PrimaryColumn()
    uuid!: string;

    @Column()
    deathcount!: number;

    @Column()
    name!: string;

    @Column()
    lastdeathreason!: string;

    @OneToMany(() => Donation, donation => donation.uuid, {
        eager: true
    })
    donations?: Donation[];

    constructor() {
        this.deathcount = 0;
    }
}
