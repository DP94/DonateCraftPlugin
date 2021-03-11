import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Player} from '../response/player';
import {Donation} from '../response/donation';
import {DeathService} from './death.service';
import {ActivatedRoute} from '@angular/router';
import {ModalComponent} from '../modal/modal.component';
import {Death} from '../response/death';
import {RevivalLock} from '../response/revival.lock';

@Component({
  selector: 'app-deaths',
  templateUrl: './deaths.component.html',
  styleUrls: ['./deaths.component.css']
})
export class DeathsComponent implements OnInit, OnDestroy, AfterViewInit {

  players: Player[];
  locks: RevivalLock[];
  paidForDonations: Set<Donation>;

  playerPing;
  @ViewChild('modal') modal: ModalComponent;

  constructor(private deathService: DeathService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.paidForDonations = new Set<Donation>();
    this.getPlayerStats();
    this.playerPing = setInterval(() => {
      this.getPlayerStats();
    }, 10000);
  }

  ngAfterViewInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      const status = params.status;
      if (status === 'success' || status === 'pending') {
        this.modal.showSuccessModal();
      } else if (status === 'error' || status === 'cancelled') {
        const key = params.key;
        this.modal.key = key;
        this.modal.showErrorModal();
      }
    });
  }

  ngOnDestroy(): void {
    clearInterval(this.playerPing);
  }

  getPlayerStats(): void {
    this.deathService.getPlayerStats().subscribe(response => {
      this.paidForDonations = new Set<Donation>();
      this.players = response.players;
    });
    this.deathService.getPlayerLocks().subscribe(response => {
      this.locks = response.revivals;
    });
  }


  getTotalDonationsForPlayer(player: Player, donations: Donation[]): string {
    let total = 0;
    for (const donation of donations) {
      // Can be done DB side, but i don't know how :(
      if (donation.paidForBy === undefined || donation.paidForBy === null) {
        total += donation.amount;
      } else {
        this.paidForDonations.add(donation);
      }
    }
    for (const donation of this.paidForDonations) {
      if (player.uuid === donation.paidForBy.uuid) {
        total += donation.amount;
      }
    }
    return total.toFixed(2);
  }

  getMostRecentDeathForUser(deaths: Death[]): string {
    if (deaths && deaths.length > 0) {
      return deaths[deaths.length - 1].reason;
    }
    return '';
  }

  getPlayerStatusFromAvailableLocks(player: Player): string {
    if (this.locks && this.locks.length > 0 && player) {
      for (const lock of this.locks) {
        if (lock.key === player.uuid) {
          return 'Dead';
        }
      }
      return 'Alive';
    }
    return 'Unknown';
  }

}
