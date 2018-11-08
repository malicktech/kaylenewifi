/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { KaylenewifiTestModule } from '../../../test.module';
import { RecordDeleteDialogComponent } from 'app/entities/record/record-delete-dialog.component';
import { RecordService } from 'app/entities/record/record.service';

describe('Component Tests', () => {
    describe('Record Management Delete Component', () => {
        let comp: RecordDeleteDialogComponent;
        let fixture: ComponentFixture<RecordDeleteDialogComponent>;
        let service: RecordService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [KaylenewifiTestModule],
                declarations: [RecordDeleteDialogComponent]
            })
                .overrideTemplate(RecordDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(RecordDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(RecordService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
