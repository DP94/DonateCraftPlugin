import {Column, Entity, PrimaryGeneratedColumn} from 'typeorm';

@Entity()
export class Death {

    @PrimaryGeneratedColumn()
    id!: number;

    @Column()
    uuid!: string;

    @Column()
    deathcount!: number;

    @Column()
    name!: string;

    @Column()
    lastdeathreason!: string;

    constructor() {
        this.deathcount = 0;
    }
}
