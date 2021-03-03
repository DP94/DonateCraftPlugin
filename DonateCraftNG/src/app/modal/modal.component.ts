import {Component, ViewChild, ViewEncapsulation} from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {environment} from '../../environments/environment';
import {Router} from '@angular/router';

@Component({
  selector: 'app-modal',
  templateUrl: './modal-options.html',
  encapsulation: ViewEncapsulation.None,
  styles: [`
    .modal-content {
      background: none;
      border: none;
    }

    .modal-content, .modal-body {
      border-radius: 3%;
      text-align: center;
      font-size: 20px;
    }

    .modal-backdrop {
      background-color: #d4d4d4;
    }

    .success-modal {
      background-color: #9be69b;
    }

    .error-modal {
      background-color: #ef6b6b;
    }

    .warning-modal {
      background-color: #ffc107;
    }

    .error-try-again {
      color: black;
      font-weight: bold;
      text-decoration: underline;
      cursor: pointer;
    }
  `]
})
export class ModalComponent {

  @ViewChild('successContent') successContent;
  @ViewChild('errorContent') errorContent;
  @ViewChild('warningContent') warningContent;

  key: string;
  modalBody: string;

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

  onErrorTryAgainClick(modal): void {
    modal.dismiss();
    this.router.navigate(['/donate', {key: this.key}]);
  }
}
