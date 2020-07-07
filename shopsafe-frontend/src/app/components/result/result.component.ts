import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../../api/api.service';
import { Store } from '../../classes/store/store';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Result } from '../../classes/result/result';
import { Router, ActivatedRoute } from '@angular/router';
import { CountyStats } from 'src/app/classes/county-stats/county-stats';

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.css']
})

@Injectable({
  providedIn: 'root'
})
export class ResultComponent implements OnInit {
  @Input() result: Result;
  location: string;
  proportion: number;

  constructor(
    private apiService: ApiService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.location = this.route.snapshot.paramMap.get('location').toString();
    this.getResult();
  }

  getResult(): void {
    console.log('CLIENT: results api call at ' + this.location);
    this.apiService.getNearbyStores(this.location)
      .subscribe(data => 
        this.result = {
          stores: (data as any).stores,
          countyStats: (data as any).countyStats
        },
        err => {
          console.log(err)
        },
        // To run getProportion function after API call
        () => {
          this.getProportion()
        }
      );
    // this.getProportion();
  }

  getProportion(): void {
    // Round proportion to 2 decimal places
    this.proportion = Math.round((this.result.countyStats[0].cases / this.result.countyStats[0].population) * 100) / 100;
    console.log("CLIENT: calculated proportion as " + this.proportion);
  }
}
