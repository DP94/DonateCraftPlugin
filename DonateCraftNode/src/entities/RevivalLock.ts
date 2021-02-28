import {Column, Entity, JoinColumn, OneToOne, PrimaryGeneratedColumn} from 'typeorm';
import {Donation} from "./donation";

@Entity()
export class RevivalLock {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    key!: string;

    @Column()
    unlockurl!: string;

    @OneToOne(type => Donation, {
        cascade: true,
    }) @JoinColumn()
    donation?: Donation;

    @Column()
    unlocked!: boolean;
}
