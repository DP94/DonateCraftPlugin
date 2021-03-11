import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {Charity} from '../response/charity';
import {DonationService} from './donation.service';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ModalComponent} from '../modal/modal.component';

@Component({
  selector: 'app-donation',
  templateUrl: './donation.component.html',
  styleUrls: ['./donation.component.css']
})
export class DonationComponent implements AfterViewInit {

  charities: Charity[];
  finishedLoading = false;
  charitiesLoaded = 0;
  playerKey = '';
  donorKey = '';

  charityIds = new Array<number>();

  @ViewChild('modal') modal: ModalComponent;

  constructor(private donationService: DonationService, private activatedRoute: ActivatedRoute) {
  }

  ngAfterViewInit(): void {
    this.charities = new Array<Charity>();
    // Can retrieve these from DB at a later time
    this.charityIds.push(13441, 2357, 255811, 2201, 182244, 233, 11200, 300, 11369, 13792);

    // For some reason Race Equality First does not exist in staging
    if (environment.production) {
      this.charityIds.push(2979600, 2536613);
    }

    this.setPlayerKeyFromURL(this.activatedRoute);
    if (this.playerKey && this.playerKey !== '') {
      this.donationService.checkIfRevivalKeyExists(this.playerKey).subscribe(exists => {
        if (!exists) {
          this.modal.showWarningModal();
        }
      });
    }

    this.charityIds.forEach((id) => {
      this.setCharityInformation(id);
    });
  }

  onJGClick(charity: Charity): void {
    let url = `${environment.justGivingDonateUrl}${charity.id}?exiturl=${environment.fullAPIUrl}callback?data=JUSTGIVING-DONATION-ID|${this.playerKey}`;
    if (this.donorKey) {
      url += `|${this.donorKey}`;
    }
    window.open(url, '_self');
  }

  setPlayerKeyFromURL(route: ActivatedRoute): void {
    route.paramMap.subscribe(params => {
      this.playerKey = params.get('key');
      this.donorKey = params.get('donorKey');
    });
  }

  setCharityInformation(id: number): void {
    console.log(`Loading ${id}`);
    this.donationService.getCharityDetails(id).subscribe(response => {
      console.log(`Loaded ${id}`);
      this.charities.push(response);
      this.charitiesLoaded++;
      console.log(`Loaded charity ${this.charitiesLoaded} out of ${this.charityIds.length}`);
      this.finishedLoading = (this.charitiesLoaded === this.charityIds.length);
      console.log(this.finishedLoading);
    });
  }

}
