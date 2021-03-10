import {Component, ViewChild, ViewEncapsulation} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Router} from '@angular/router';
import {Player} from '../response/player';

@Component({
  selector: 'app-modal',
  templateUrl: './modal-options.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['modal-options.css']
})
export class ModalComponent {

  @ViewChild('successContent') successContent;
  @ViewChild('errorContent') errorContent;
  @ViewChild('warningContent') warningContent;
  @ViewChild('donateContent') donateContent;

  key: string;
  modalBody: string;
  players: Player[];

  constructor(private modalService: NgbModal, private router: Router) {}

  showSuccessModal(): void {
    this.modalService.open(this.successContent);
  }

  showWarningModal(): void {
    this.modalService.open(this.warningContent);
  }

  showWarningModalWithText(text): void {
    this.modalBody = text;
    this.modalService.open(this.warningContent);
  }

  showErrorModal(): void {
    this.modalService.open(this.errorContent);
  }

  showDonateModal(deadPlayerKey: string, players: Player[]): void {
    this.players = players;
    this.key = deadPlayerKey;
    this.modalService.open(this.donateContent);
  }

  onErrorTryAgainClick(modal): void {
    modal.dismiss();
    this.router.navigate(['/donate', {key: this.key}]);
  }

  onDonatePlayerClick(modal, donor): void {
    modal.dismiss();
    let options = {};
    if (this.key !== donor) {
      options = {key: this.key, donorKey: donor};
    } else {
      options = {key: this.key};
    }
    this.router.navigate(['/donate', options]);
  }
}
