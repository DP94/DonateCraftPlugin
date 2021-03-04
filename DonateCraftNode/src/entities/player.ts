import {Column, Entity, OneToMany, PrimaryColumn} from 'typeorm';
import {Donation} from "./donation";
import {Death} from "./death";

@Entity()
export class Player {

    @PrimaryColumn()
    uuid!: string;

    @Column()
    name!: string;

    @OneToMany(() => Donation, donation => donation.player, {
        eager: true
    })
    donations?: Donation[];

    @OneToMany(() => Death, death => death.player, {
        cascade: true,
        eager: true
    })
    deaths!: Death[];
}
