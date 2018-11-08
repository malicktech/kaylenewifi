import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Record } from 'app/shared/model/record.model';
import { RecordService } from './record.service';
import { RecordComponent } from './record.component';
import { RecordDetailComponent } from './record-detail.component';
import { RecordUpdateComponent } from './record-update.component';
import { RecordDeletePopupComponent } from './record-delete-dialog.component';
import { IRecord } from 'app/shared/model/record.model';

@Injectable({ providedIn: 'root' })
export class RecordResolve implements Resolve<IRecord> {
    constructor(private service: RecordService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Record> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Record>) => response.ok),
                map((record: HttpResponse<Record>) => record.body)
            );
        }
        return of(new Record());
    }
}

export const recordRoute: Routes = [
    {
        path: 'record',
        component: RecordComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'kaylenewifiApp.record.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'record/:id/view',
        component: RecordDetailComponent,
        resolve: {
            record: RecordResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'kaylenewifiApp.record.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'record/new',
        component: RecordUpdateComponent,
        resolve: {
            record: RecordResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'kaylenewifiApp.record.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'record/:id/edit',
        component: RecordUpdateComponent,
        resolve: {
            record: RecordResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'kaylenewifiApp.record.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const recordPopupRoute: Routes = [
    {
        path: 'record/:id/delete',
        component: RecordDeletePopupComponent,
        resolve: {
            record: RecordResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'kaylenewifiApp.record.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
