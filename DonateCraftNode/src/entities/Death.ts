import {Entity, Column, PrimaryGeneratedColumn} from 'typeorm';

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
}
