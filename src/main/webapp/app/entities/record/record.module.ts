import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { KaylenewifiSharedModule } from 'app/shared';
import {
    RecordComponent,
    RecordDetailComponent,
    RecordUpdateComponent,
    RecordDeletePopupComponent,
    RecordDeleteDialogComponent,
    recordRoute,
    recordPopupRoute
} from './';

const ENTITY_STATES = [...recordRoute, ...recordPopupRoute];

@NgModule({
    imports: [KaylenewifiSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [RecordComponent, RecordDetailComponent, RecordUpdateComponent, RecordDeleteDialogComponent, RecordDeletePopupComponent],
    entryComponents: [RecordComponent, RecordUpdateComponent, RecordDeleteDialogComponent, RecordDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class KaylenewifiRecordModule {}
