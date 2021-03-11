import {Column, Entity, OneToMany, PrimaryGeneratedColumn} from "typeorm";
import {Death} from "./death";
import {Donation} from "./donation";

@Entity()
export class Session {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    name!: string;

    @Column()
    dateStarted!: Date;

    @Column()
    dateFinished?: Date;

    @OneToMany(() => Death, death => death.session)
    deaths?: Death[];

    @OneToMany(() => Donation, donation => donation.session)
    donations?: Donation[];
}