import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DonationComponent } from './donation.component';
import {ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, ParamMap, Params} from '@angular/router';
import {of, ReplaySubject} from 'rxjs';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {DonationService} from './donation.service';

describe('DonationComponent', () => {
  let component: DonationComponent;
  let fixture: ComponentFixture<DonationComponent>;
  let routeStub;

  beforeEach(async () => {
    routeStub = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ DonationComponent ],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub},
        DonationService
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DonationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Should get player key from url parameter', () => {
      routeStub.setParamMap({key: 'test'});
      component.setPlayerKeyFromURL(routeStub);
      expect(component.playerKey).toEqual('test');
  });


  class ActivatedRouteStub implements Partial<ActivatedRoute> {
    // tslint:disable-next-line:variable-name
    private _paramMap: ParamMap;
    private subject = new ReplaySubject<ParamMap>();

    paramMap = this.subject.asObservable();
    get snapshot(): ActivatedRouteSnapshot {
      const snapshot: Partial<ActivatedRouteSnapshot> = {
        paramMap: this._paramMap,
      };

      return snapshot as ActivatedRouteSnapshot;
    }

    constructor(initialParams?: Params) {
      this.setParamMap(initialParams);
    }

    setParamMap(params?: Params): void {
      const paramMap = convertToParamMap(params);
      this._paramMap = paramMap;
      this.subject.next(paramMap);
    }
  }
});
