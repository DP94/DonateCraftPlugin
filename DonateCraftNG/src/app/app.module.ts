import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {HttpClientModule} from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DonationComponent } from './donation/donation.component';
import {AppRoutingModule} from './app-routing.module';
import { DeathsComponent } from './deaths/deaths.component';

@NgModule({
  declarations: [
    AppComponent,
    DonationComponent,
    DeathsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    AppRoutingModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
