/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { KaylenewifiTestModule } from '../../../test.module';
import { RecordUpdateComponent } from 'app/entities/record/record-update.component';
import { RecordService } from 'app/entities/record/record.service';
import { Record } from 'app/shared/model/record.model';

describe('Component Tests', () => {
    describe('Record Management Update Component', () => {
        let comp: RecordUpdateComponent;
        let fixture: ComponentFixture<RecordUpdateComponent>;
        let service: RecordService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [KaylenewifiTestModule],
                declarations: [RecordUpdateComponent]
            })
                .overrideTemplate(RecordUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(RecordUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(RecordService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Record(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.record = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Record();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.record = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
