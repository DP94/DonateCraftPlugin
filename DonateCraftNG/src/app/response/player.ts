import {Donation} from './donation';
import {Death} from './death';

export class Player {
  uuid: string;
  id: number;
  name: string;
  deaths: Death[];
  donations: Donation[];
}
