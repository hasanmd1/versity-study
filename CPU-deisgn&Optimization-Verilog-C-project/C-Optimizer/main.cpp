#include <cstdio>
#include <cstdlib>
#include <ctime>

#define RGB_SIZE 3

struct Image
{
    size_t weight;
    size_t height;
    size_t depth;
};

struct Histogram
{
    unsigned int value0to50;
    unsigned int value51to101;
    unsigned int value102to152;
    unsigned int value153to203;
    unsigned int value204to255;
};


struct Pixel
{
    size_t red;
    size_t green;
    size_t blue;
};

unsigned int grayScaleCalculator( Pixel pixel ){
    return round( 0.2126*(pixel.red) + 0.7152*(pixel.green) + 0.0722*(pixel.blue));
}

Pixel pixelCalculator(unsigned char ** ImageStorageArray, size_t i, size_t j)
{
    Pixel pixel;
    pixel.red = ImageStorageArray[i][j];
    pixel.green = ImageStorageArray[i][j+1];
    pixel.blue = ImageStorageArray[i][j+2];
    return pixel;
}

void histogramInput( Histogram *histogram , unsigned int y ){
    if ( y <= 50 )
        ++( histogram->value0to50 );
    else if ( y <= 101 )
        ++( histogram->value51to101 );
    else if ( y <= 152 )
        ++( histogram->value102to152 );
    else if ( y <= 203 )
        ++(histogram->value153to203 );
    else
        ++( histogram->value204to255 );
}

int optimizeSaturationValue(int input){
    if(input < 0)
        return 0;
    if(input > 255)
        return 255;
    return input;
}

void WriteImage(FILE *outputPpmFile, unsigned char **ImageStorageArray, Image *rawData, Histogram *histogram)
{
    // Write header
    fprintf(outputPpmFile, "P6\n%ld\n%ld\n%ld\n", rawData->weight, rawData->height, rawData->depth);

    // Write pixels and update histogram
    for (size_t i = 0; i < rawData->weight * RGB_SIZE; i += 3)
    {
        Pixel pixel = pixelCalculator(ImageStorageArray, 0, i);
        histogramInput(histogram, grayScaleCalculator(pixel));
        fprintf(outputPpmFile, "%c%c%c", (unsigned char)pixel.red, (unsigned char)pixel.green, (unsigned char)pixel.blue);
    }

    for (size_t i = 1; i < rawData->height - 1; ++i)
    {
        fprintf(outputPpmFile, "%c%c%c", ImageStorageArray[i][0], ImageStorageArray[i][1], ImageStorageArray[i][2]);
        Pixel pixel = pixelCalculator(ImageStorageArray, i, 0);
        histogramInput(histogram, grayScaleCalculator(pixel));
        for (size_t j = 3; j < (rawData->weight - 1) * RGB_SIZE; j += 3)
        {
            Pixel centralPixel = pixelCalculator(ImageStorageArray, i, j);
            Pixel upperPixel = pixelCalculator(ImageStorageArray, i - 1, j);
            Pixel lowerPixel = pixelCalculator(ImageStorageArray, i, j - 3);
            Pixel rightPixel = pixelCalculator(ImageStorageArray, i, j + 3);
            Pixel downPixel = pixelCalculator(ImageStorageArray, i + 1, j);
            Pixel newPixel;
            newPixel.red = optimizeSaturationValue(5 * centralPixel.red - (upperPixel.red + lowerPixel.red + rightPixel.red + downPixel.red));
            newPixel.green = optimizeSaturationValue(5 * centralPixel.green - (upperPixel.green + lowerPixel.green + rightPixel.green + downPixel.green));
            newPixel.blue = optimizeSaturationValue(5 * centralPixel.blue - (upperPixel.blue + lowerPixel.blue + rightPixel.blue + downPixel.blue));
            fprintf(outputPpmFile, "%c%c%c", (unsigned char)newPixel.red, (unsigned char)newPixel.green, (unsigned char)newPixel.blue);
            histogramInput(histogram, grayScaleCalculator(newPixel));
        }
        fprintf(outputPpmFile, "%c%c%c", ImageStorageArray[i][(rawData->weight - 1) * RGB_SIZE], ImageStorageArray[i][(rawData->weight - 1) * RGB_SIZE + 1], ImageStorageArray[i][(rawData->weight - 1) * RGB_SIZE + 2]);
        pixel = pixelCalculator(ImageStorageArray, i, (rawData->weight - 1) * RGB_SIZE);
        histogramInput(histogram, grayScaleCalculator(pixel));
    }

    for (size_t i = 0; i < rawData->weight * RGB_SIZE; i += 3)
    {
        Pixel pixel = pixelCalculator(ImageStorageArray, rawData->height - 1, i);
        histogramInput(histogram, grayScaleCalculator(pixel));
        fprintf(outputPpmFile, "%c%c%c", (unsigned char)pixel.red, (unsigned char)pixel.green, (unsigned char)pixel.blue);
    }
}


bool HeaderScanner( FILE * inputtedFile, Image * rawData ){
    fseek( inputtedFile , 3 , SEEK_SET );
    if ( fscanf( inputtedFile , "%lu %lu\n%lu\n" , &(rawData->weight) , &(rawData->height) , &(rawData->depth) ) !=3 ){
        return false;
    }
    return true;
}
void FreeImageStorage( unsigned char ** ImageStorageArray , const Image * rawData ){
    if( !ImageStorageArray )
        return;
    for( size_t i = 0 ; i < rawData->height ; i++ ){
        free(ImageStorageArray[i]);
    }
    free(ImageStorageArray);
}

bool UpdateOutputTxtFile(const Histogram * histogram){
    FILE * outputTxtFile = fopen("output.txt","w");
    if(fprintf(outputTxtFile,"%d %d %d %d %d", histogram->value0to50, histogram->value51to101, histogram->value102to152, histogram->value153to203, histogram->value204to255) != 5)
        return false;
    fclose(outputTxtFile);
    return true;
}

bool ImageScanner( unsigned char ** ImageStorageArray , Image * rawData , FILE * inputtedFile ){
    for( size_t i = 0 ; i < rawData->height ; ++i )
    {
        ImageStorageArray[i] = ( unsigned  char * ) malloc( rawData->weight * RGB_SIZE );
        if ( fread( ImageStorageArray[i] , sizeof ( unsigned char ), rawData->weight * RGB_SIZE, inputtedFile ) == 0)
            return false;
    }
    fclose(inputtedFile);
    return true;
}


int main( int argc , char ** argv ){

    struct timespec first, last;
    clock_gettime( CLOCK_REALTIME, &first);

    FILE * inputtedFile;
    Image rawData;
    Histogram histogram;
    histogram.value0to50 = histogram.value51to101 = histogram.value102to152 = histogram.value153to203 = histogram.value204to255 = 0;

    //if arg counter != 2 throw error
    if ( argc != 2 ){
        return -1;
    }

    //if unable to open file throw error
    inputtedFile = fopen( argv[1] , "rb" );
    if(!inputtedFile){
        return -1;
    }
    //in ppm first three lines contain weight, height and depth we use that for header
    if(!HeaderScanner(inputtedFile, &rawData)){
        return -1;
    }

    //here we will copy the image to out ImageStorageArray
    unsigned char ** ImageStorageArray = ( unsigned  char ** ) malloc( rawData.height * sizeof( unsigned char * ) );
    if(!ImageScanner(ImageStorageArray, &rawData, inputtedFile)){
        return -1;
    }
    fclose(inputtedFile);

    //Write in output.ppm file
    FILE * outputPpmFile = fopen("output.ppm","wb");
    if(!outputPpmFile){
        return -1;
    }
    WriteImage(outputPpmFile, ImageStorageArray, &rawData, &histogram);
    fclose(outputPpmFile);

    //Free the ImageStorageArray
    FreeImageStorage(ImageStorageArray, &rawData);

    //now write the stored value of histogram in output.txt file
    if(!UpdateOutputTxtFile(&histogram)){
        return -1;
    }
    clock_gettime( CLOCK_REALTIME, &last);
    double totTime = ( last.tv_sec - first.tv_sec )*1000.0 + ( last.tv_nsec - first.tv_nsec )/ 1000000.0;
    printf( "Time: %.6lf ms\n", totTime );

    return 0;
}