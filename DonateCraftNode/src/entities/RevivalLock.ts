import {Entity, Column, PrimaryGeneratedColumn} from 'typeorm';

@Entity()
export class RevivalLock {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    key!: string;

    @Column()
    reference!: string;

    @Column()
    unlockurl!: string;

    @Column()
    donationid!: number;

    @Column()
    unlocked!: boolean;

    @Column()
    value!: number;
}
