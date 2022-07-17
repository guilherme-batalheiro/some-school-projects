#include <iostream>
#include <unordered_map>
#include <vector>

int readVector(std::vector<int> *vec) {
    int intRead, size = 0;
    bool neg = false, read = false;
    char c = '\0';

    do {
        intRead = 0;
        read = false;

        c = getchar();
        if (c == '-') {
            neg = true;
            c = getchar();
        }

        for (; (c > 47 && c < 58); c = getchar()) {
            intRead = intRead * 10 + c - 48;
            read = true;
        }

        if (neg) intRead *= -1;

        if (read) {
            (*vec).push_back(intRead);
            size++;
        }
    } while (c != EOF && c != '\n');

    return size;
}

void solveProbOne() {
    std::vector<int> x;
    int size = readVector(&x);

    if (size == 0) {
        std::cout << 0 << ' ' << 0 << '\n';
        return;
    }

    int arrSize[size];
    int arrNumbOfSeq[size];

    for (int i = 0; i < size; i++) {
        arrSize[i] = 1;
        arrNumbOfSeq[i] = 1;
    }

    long int maxSize = 0;
    long int numberOfSeq = 0;

    long int lastMaxSize = 0;
    long int lastNumbOfSeq = 0;

    for (int i = 1; i < size; i++) {
        lastMaxSize = arrSize[i];
        lastNumbOfSeq = arrNumbOfSeq[i];
        for (int j = 0; j < i; j++) {
            if (x[i] > x[j]) {
                if (arrSize[j] + 1 > lastMaxSize) {
                    lastMaxSize = arrSize[j] + 1;
                    lastNumbOfSeq = arrNumbOfSeq[j];
                } else if (arrSize[j] + 1 == lastMaxSize) {
                    lastNumbOfSeq += arrNumbOfSeq[j];
                }
            }
        }
        arrSize[i] = lastMaxSize;
        arrNumbOfSeq[i] = lastNumbOfSeq;
    }

    for (int i = 0; i < size; i++) {
        if (arrSize[i] > maxSize) {
            maxSize = arrSize[i];
            numberOfSeq = arrNumbOfSeq[i];
        } else if (arrSize[i] == maxSize) {
            numberOfSeq += arrNumbOfSeq[i];
        }
    }

    std::cout << maxSize << ' ' << numberOfSeq << '\n';
}

int readVectorAndCreatMap(std::vector<int> *vec,
                          std::unordered_map<int, int> *umap) {
    int intRead, size = 0;
    bool neg = false, read = false;
    char c = '\0';

    do {
        intRead = 0;
        read = false;

        c = getchar();
        if (c == '-') {
            neg = true;
            c = getchar();
        }

        for (; (c > 47 && c < 58); c = getchar()) {
            intRead = intRead * 10 + c - 48;
            read = true;
        }

        if (neg) intRead *= -1;

        if (read) {
            if ((*umap).find(intRead) == (*umap).end())
                (*umap).insert({intRead, size});
            (*vec).push_back(intRead);
            size++;
        }
    } while (c != EOF && c != '\n');

    return size;
}

int readVectorCreatMapAndFilter(std::vector<int> *vec,
                                std::unordered_map<int, int> *umap,
                                std::unordered_map<int, int> filter) {
    int intRead, size = 0;
    bool neg = false, read = false;
    char c = '\0';

    do {
        intRead = 0;
        read = false;

        c = getchar();
        if (c == '-') {
            neg = true;
            c = getchar();
        }

        for (; (c > 47 && c < 58); c = getchar()) {
            intRead = intRead * 10 + c - 48;
            read = true;
        }

        if (neg) intRead *= -1;

        if (read && filter.find(intRead) != filter.end()) {
            if ((*umap).find(intRead) == (*umap).end())
                (*umap).insert({intRead, size});
            (*vec).push_back(intRead);
            size++;
        }
    } while (c != EOF && c != '\n');

    return size;
}

int filterVectorWithMap(std::vector<int> *res, std::vector<int> x, int x_size,
                        std::unordered_map<int, int> x_umap) {
    int size = 0;
    for (int i = 0; i < x_size; i++) {
        if (x_umap.find(x[i]) != x_umap.end()) {
            (*res).push_back(x[i]);
            size++;
        }
    }

    return size;
}

void solveProbTwo() {
    std::unordered_map<int, int> aux_umap;
    std::vector<int> aux;
    int aux_size = readVectorAndCreatMap(&aux, &aux_umap);

    std::unordered_map<int, int> y_umap;
    std::vector<int> y;
    int y_size = readVectorCreatMapAndFilter(&y, &y_umap, aux_umap);

    aux_umap.clear();

    std::vector<int> x;
    int x_size = filterVectorWithMap(&x, aux, aux_size, y_umap);

    y_umap.clear();

    std::vector<int> arr(y_size, 0);

    int maxSize = 0;

    for (int i = 0; i < x_size; i++) {
        int previousBiggerSize = 0;
        for (int j = 0; j < y_size; j++) {
            if (x[i] > y[j] && arr[j] > previousBiggerSize) {
                previousBiggerSize = arr[j];
            }

            else if (x[i] == y[j] && previousBiggerSize + 1 > arr[j]) {
                arr[j] = previousBiggerSize + 1;
                if (previousBiggerSize + 1 > maxSize)
                    maxSize = previousBiggerSize + 1;
            }
        }
    }

    std::cout << maxSize << '\n';
}

int main() {
    char problemNum;
    problemNum = getchar();
    getchar();

    if (problemNum == '1')
        solveProbOne();
    else if (problemNum == '2')
        solveProbTwo();

    return 0;
}