interface Sector {
    id: number;
    name: string;
    children: Sector[];
    level?: number;
}
