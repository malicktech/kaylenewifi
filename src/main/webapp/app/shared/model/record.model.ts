import { IEvent } from 'app/shared/model//event.model';

export interface IRecord {
    id?: number;
    phone?: string;
    code?: string;
    firstName?: string;
    lastName?: string;
    address?: string;
    email?: string;
    event?: IEvent;
}

export class Record implements IRecord {
    constructor(
        public id?: number,
        public phone?: string,
        public code?: string,
        public firstName?: string,
        public lastName?: string,
        public address?: string,
        public email?: string,
        public event?: IEvent
    ) {}
}
