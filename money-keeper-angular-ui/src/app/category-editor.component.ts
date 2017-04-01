import {OnInit, Component} from "@angular/core";
import {ExtendedCategory} from "./model/extended.category";
import {CategoriesService} from "./service/category.service";

@Component({
    moduleId: module.id,
    selector: 'category-editor',
    templateUrl: './html/category-editor.component.html',
    styleUrls: ['./css/category-editor.component.css']
})
export class CategoryEditorComponent implements OnInit {
    categories: ExtendedCategory[];

    constructor(private categoriesService: CategoriesService) {
    }

    ngOnInit(): void {
        this.categoriesService.getCategories()
            .then(categories => this.categories = categories);
        $('body').on('click', '.show-stores', function (e) {
            e.preventDefault();
            var $this = $(this);
            if ($this.prop('closed') === false) {
                $this.text('Show stores »');
                $this.prop('closed', true);
            } else {
                $this.text('Hide stores «');
                $this.prop('closed', false);
            }
            var collapse = $this.parent().find('.stores');
            collapse.collapse('toggle');
        });
    }

    delete(category: ExtendedCategory): void {
        this.categoriesService
            .delete(category.name)
            .then(() => {
                this.categories = this.categories.filter(c => c !== category);
            });
    }

    openDeleteDialog(category: ExtendedCategory): void {
        let modal = $('.category-delete-modal');
        modal.find('.category-delete-name').text(category.name);
        modal.modal('show');
        let comp = this;
        modal.find('.category-delete-apply')
            .unbind('click')
            .click(function () {
                comp.delete(category);
                modal.modal('hide');
            });
    }

}