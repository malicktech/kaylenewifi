import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { KaylenewifiEventModule } from './event/event.module';
import { KaylenewifiRecordModule } from './record/record.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        KaylenewifiEventModule,
        KaylenewifiRecordModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class KaylenewifiEntityModule {}
