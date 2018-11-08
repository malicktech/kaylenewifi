import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IRecord } from 'app/shared/model/record.model';
import { RecordService } from './record.service';
import { IEvent } from 'app/shared/model/event.model';
import { EventService } from 'app/entities/event';

@Component({
    selector: 'jhi-record-update',
    templateUrl: './record-update.component.html'
})
export class RecordUpdateComponent implements OnInit {
    record: IRecord;
    isSaving: boolean;

    events: IEvent[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private recordService: RecordService,
        private eventService: EventService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ record }) => {
            this.record = record;
        });
        this.eventService.query().subscribe(
            (res: HttpResponse<IEvent[]>) => {
                this.events = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.record.id !== undefined) {
            this.subscribeToSaveResponse(this.recordService.update(this.record));
        } else {
            this.subscribeToSaveResponse(this.recordService.create(this.record));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IRecord>>) {
        result.subscribe((res: HttpResponse<IRecord>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackEventById(index: number, item: IEvent) {
        return item.id;
    }
}
