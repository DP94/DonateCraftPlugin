import {Column, Entity, PrimaryGeneratedColumn} from 'typeorm';

@Entity()
export class RevivalLock {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    key!: string;

    @Column()
    unlockurl!: string;

    @Column()
    donationid!: number;

    @Column()
    unlocked!: boolean;
}
