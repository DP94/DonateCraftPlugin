import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {Charity} from '../response/charity';
import {DonationService} from './donation.service';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
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

  charityIds = new Array<number>();

  @ViewChild('modal') modal: ModalComponent;

  constructor(private donationService: DonationService, private activatedRoute: ActivatedRoute) {
  }

  ngAfterViewInit(): void {
    this.charities = new Array<Charity>();
    // Can retrieve these from DB at a later time
    this.charityIds.push(13441, 2357, 255811, 2201, 182244, 233, 11200, 300);

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
    window.open(environment.justGivingDonateUrl + charity.id + '?exiturl=' + environment.fullAPIUrl
                                                    + 'callback?data=JUSTGIVING-DONATION-ID|'
                                                    + this.playerKey, '_self');
  }

  setPlayerKeyFromURL(route: ActivatedRoute): void {
    route.paramMap.subscribe(params => {
      this.playerKey = params.get('key');
    });
  }

  setCharityInformation(id: number): void {
    this.donationService.getCharityDetails(id).subscribe(response => {
      this.charities.push(response);
      this.charitiesLoaded++;
      this.finishedLoading = (this.charitiesLoaded === this.charityIds.length);
    });
  }

}
