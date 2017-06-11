import {Category} from "./category";
import {Account} from "./account";
import {Transaction} from "./transaction";

export class Budget {
    accounts: Account[];
    categories: Category[];
    name: string;
    from: string;
    to: string;
    amount: number;

    spentAmount: number;
    progress: number;
    transactions: Transaction[];
}