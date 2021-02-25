import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DonationComponent} from './donation/donation.component';
import {DeathsComponent} from './deaths/deaths.component';


const routes: Routes = [
  {path: '', component: DeathsComponent}, {path: 'deaths', component: DeathsComponent}, {path: 'donate', component: DonationComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
