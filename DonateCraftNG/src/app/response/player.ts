import {Donation} from './donation';

export class Player {
  deathcount: number;
  uuid: string;
  id: number;
  name: string;
  lastdeathreason: string;
  donations: Donation[];
}
