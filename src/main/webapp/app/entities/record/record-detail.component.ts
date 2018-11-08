import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRecord } from 'app/shared/model/record.model';

@Component({
    selector: 'jhi-record-detail',
    templateUrl: './record-detail.component.html'
})
export class RecordDetailComponent implements OnInit {
    record: IRecord;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ record }) => {
            this.record = record;
        });
    }

    previousState() {
        window.history.back();
    }
}
