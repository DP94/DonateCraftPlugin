import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Player} from '../response/player';
import {Donation} from '../response/donation';
import {DeathService} from './death.service';
import {ActivatedRoute} from '@angular/router';
import {ModalComponent} from '../modal/modal.component';
import {Death} from '../response/death';

@Component({
  selector: 'app-deaths',
  templateUrl: './deaths.component.html',
  styleUrls: ['./deaths.component.css']
})
export class DeathsComponent implements OnInit, OnDestroy, AfterViewInit {

  players: Player[];
  playerPing;
  @ViewChild('modal') modal: ModalComponent;

  constructor(private deathService: DeathService, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.getPlayerStats();
    this.playerPing = setInterval(() => {
      this.getPlayerStats();
    },  10000);
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
      this.players = response.players;
    });
  }


  getTotalDonationsForPlayer(donations: Donation[]): number {
    let total = 0;
    for (const donation of donations) {
      total += donation.amount;
    }
    return total;
  }

  getMostRecentDeathForUser(deaths: Death[]): string {
    if (deaths && deaths.length > 0) {
      return deaths[0].reason;
    }
    return '';
  }

}
