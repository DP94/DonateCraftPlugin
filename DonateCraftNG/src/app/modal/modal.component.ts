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

  key: string;

  constructor(private modalService: NgbModal, private router: Router) {}

  showSuccessModal(): void {
    this.modalService.open(this.successContent);
  }

  showErrorModal(): void {
    this.modalService.open(this.errorContent);
  }

  onErrorTryAgainClick(modal): void {
    modal.dismiss();
    this.router.navigate(['/donate', {key: this.key}]);
  }
}
