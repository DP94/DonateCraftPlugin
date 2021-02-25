import { TestBed } from '@angular/core/testing';

import { DeathServiceService } from './death-service.service';

describe('DeathServiceService', () => {
  let service: DeathServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeathServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
