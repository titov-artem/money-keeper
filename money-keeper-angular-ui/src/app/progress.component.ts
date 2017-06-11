import {Component, Input} from "@angular/core";
import Timer = NodeJS.Timer;

@Component({
    moduleId: module.id,
    selector: 'progress-bar',
    templateUrl: './html/progress.component.html'
})
export class ProgressComponent {

    static MAX = 10000000;
    static MIN = -1;

    // public static SUCCESS = new Gradation(ProgressComponent.MAX, ProgressComponent.MAX, ProgressComponent.MAX);
    // public static INFO = new Gradation(ProgressComponent.MIN, ProgressComponent.MAX, ProgressComponent.MAX);
    // public static WARNING = new Gradation(ProgressComponent.MIN, ProgressComponent.MIN, ProgressComponent.MAX);
    // public static DANGER = new Gradation(ProgressComponent.MIN, ProgressComponent.MIN, ProgressComponent.MIN);

    @Input() progress: number;
    @Input() subtitle: string;
    @Input() gradation: Gradation;

    showProgress(progress: number, subtitle: string, gradation: Gradation): void {
        this.progress = progress;
        this.subtitle = subtitle;
        this.gradation = gradation;
    }

    getClasses(): string {
        let addClass = '';
        if (this.progress != null && this.gradation != null) {
            let progress = this.progress;
            if (progress > ProgressComponent.MAX) progress = ProgressComponent.MAX;
            if (progress < ProgressComponent.MIN) progress = ProgressComponent.MIN;
            addClass = this.gradation.getClass(progress);
        }
        return 'progress-bar ' + addClass;
    }

    getProgress(): number {
        if (this.progress < 0) return 0;
        if (this.progress > 100) return 100;
        return this.progress;
    }
}

export class Gradation {
    private success: number;
    private info: number;
    private warning: number;

    constructor(success: number, info: number, warning: number) {
        this.success = success;
        this.info = info;
        this.warning = warning;
    }

    getClass(progress: number): string {
        if (progress <= this.success) return 'progress-bar-success';
        if (progress <= this.info) return 'progress-bar-info';
        if (progress <= this.warning) return 'progress-bar-warning';
        return 'progress-bar-danger';
    }
}