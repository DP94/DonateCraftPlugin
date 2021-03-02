import {Column, Entity, JoinColumn, OneToOne, PrimaryGeneratedColumn} from 'typeorm';
import {Donation} from "./donation";

@Entity()
export class RevivalLock {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    key!: string;

    @OneToOne(type => Donation, {
        cascade: true,
        eager: true
    }) @JoinColumn()
    donation?: Donation;

    @Column()
    unlocked!: boolean;
}
