import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LandingComponent } from './components/landing/landing.component';
import { ResultComponent } from './components/result/result.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { StoreComponent } from './components/store/store.component';
import { AboutComponent } from './components/about/about.component';
import { FeedbackFormsComponent } from './components/feedback-forms/feedback-forms.component';
import { MobileRedirectComponent } from './components/mobile-redirect/mobile-redirect.component';

const routes: Routes = [
  { 
    path: '', 
    component: LandingComponent 
  },
  {
    path: 'result/:location/:latlng', 
    component: ResultComponent, 
  },
  { 
    path: 'store/:id', 
    component: StoreComponent,
  },
  { 
    path: 'about', 
    component: AboutComponent 
  },
  {
    path: "feedback",
    component: FeedbackFormsComponent 
  },
  {
    path: "mobile",
    component: MobileRedirectComponent
  },
  { 
    path: "**", 
    component: PageNotFoundComponent 
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }