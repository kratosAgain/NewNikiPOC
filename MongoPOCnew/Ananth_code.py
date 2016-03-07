import json, sys, os
import numpy as np
import string
import random
from copy import deepcopy as copyof

DEBUG = 1
ENTITY_SIZE_PROB = range(90,100) + range(100, 1, -1)
CONCEPT_SIZE_PROB = range(10, 15) + range(15, 1, -1)
ENTITY_GRAMMAR_LENGTH_PROB = range(20, 1, -4)
CONCEPT_GRAMMAR_LENGTH_PROB = range(10, 20, 3) + range(20,1, -4)
TEST_CASES_LENGTH_PROB = range(100,1,-4)


def id_generator(size=6, chars=string.ascii_lowercase):
    return ''.join(random.choice(chars) for _ in range(size))

def cumsum(weights):
    if DEBUG>3: print >> sys.stderr, type(weights)
    sw = max(float(sum(weights)), 1e-35)
    total = 0.
    for w in weights:
        yield (total + w)/sw
        total += w

def histogram_sample(weights, cumsummed = False):
    if not cumsummed: weights = cumsum(weights)
    r = np.random.random_sample()
    for i, w in enumerate(weights):
        if w > r:
            return i
    return 0

def histogram_sample_list(weights, num = 1, input_array = None):
    weights = list(cumsum(list(weights)))
    if not input_array: input_array = range(len(weights))
    for n in range(num):
        i = histogram_sample(weights, True)
        try:
            yield input_array[i]
        except:
            logger.error("The dimensions of input_array are %s and weights is %s", len(input_array), len(weights))
            raise

def grammar_generator(num_words, word_list, word_weights):
    return ' '.join(list(histogram_sample_list(word_weights, num_words, word_list)))




def concept_generator(num_grammars, word_list, word_weights, grammar_length_probs):
    ret_concept = {}
    for n in range(num_grammars):
        gram_len = histogram_sample(grammar_length_probs) + 1
        gram = grammar_generator(gram_len, word_list, word_weights)
        ret_concept[gram] = np.random.random_sample()
    return ret_concept

def word_generator():
    """
    returns word and weight in interval [0.0, 1.0)
    """
    word_char_len = histogram_sample(range(20,1,-1)) + 1
    return id_generator(word_char_len), np.random.random_sample()
    
def test_case_generator(num_words,word_list,word_weights):
    testlist = histogram_sample_list(word_weights, num_words, word_list)
    s = set(testlist)
    return_test_list=[]
    for element in s:
        return_test_list.append(element);
        
    return ' '.join(list(return_test_list))
    
def test_generator(word_list,word_weights,utterence):
    with open('test.txt','w') as testFile:
        for i in  range(1,utterence):
            test_len = histogram_sample(TEST_CASES_LENGTH_PROB) + 1
            test = test_case_generator(test_len, word_list, word_weights)
            #s = set(test)
            #testlist=""
            #for word in s:
             #   testlist+=word;
            testFile.write(test)
            testFile.write("\n")       
       

def get_word_list(num_words, filename = None):
    word_list = []
    word_weights = []
    if filename is None:
        for n in range(num_words):
            wl, ww = word_generator()
            word_list.append(wl)
            word_weights.append(ww)
    else:
        with open(filename,'r') as fp:
            for line in fp.readlines():
                sp = line.strip().split()
                word_list.append(sp[0])
                if len(sp) > 1:
                    word_weights.append(sp[1])
                else:
                    word_weights.append(np.random.random_sample())
    return word_list, word_weights

def main(
         outputfile,
         num_words = 1000, num_concepts = 10, num_entities = 2, 
         concept_size_probs = CONCEPT_SIZE_PROB,
         concept_grammar_length_prob = CONCEPT_GRAMMAR_LENGTH_PROB,
         entity_size_probs = ENTITY_SIZE_PROB, 
         entity_grammar_length_prob = ENTITY_GRAMMAR_LENGTH_PROB,
         test_cases_length_prob = TEST_CASES_LENGTH_PROB,
         wordlistfile = None,
         utterence = 1000,
        ):
    
    
    main_json = {}
    # Initialize the word list and word weights
    word_list, word_weights = get_word_list(num_words, wordlistfile)
    # Get entities
    
    test_generator(word_list,word_weights,utterence)
    
    entity_names = []
    for entity in range(num_entities):
        entity_name = '_e_' + id_generator(4)
        entity_size = histogram_sample(entity_size_probs) + 1
        main_json[entity_name] = concept_generator(entity_size, word_list, word_weights, entity_grammar_length_prob)
        entity_names.append(entity_name)
    # Add entity name to word list with high probability
    s = sum(word_weights) 
    m = max(word_weights)
    for e in entity_names:
        entity_weight = np.random.random_integers(m, s-m)
        word_list.append(e)
        word_weights.append(entity_weight)
    # Get concepts
    for concept in range(num_concepts):
        concept_name = '_c_' + id_generator(4)
        concept_size = histogram_sample(concept_size_probs) + 1
        main_json[concept_name] = concept_generator(concept_size, word_list, word_weights, concept_grammar_length_prob)
        s = sum(word_weights) 
        m = max(word_weights)
        # Add concept to the word list with high prob so that it may be picked up in higher level concepts
        concept_weight = np.random.random_integers(m, s-m)   # Prob of concept keeps getting higher as it climbs the hierarchy
        word_list.append(concept_name)
        word_weights.append(concept_weight)
    # Write the output to file
    with open(outputfile, 'w') as fp:
        json.dump(main_json, fp, indent = 4)
    
    
        
        
    

if __name__ == '__main__':
    from optparse import OptionParser
    parser = OptionParser("Module which is used to simulate a Concept-Grammar-Entity Language Model")
    parser.add_option('-w', '--num-words', dest = 'num_words', default = 1000,
                      help = 'Number of words in the vocabulary [default=%default]')
    parser.add_option('-c', '--num-concepts', dest = 'num_concepts', default = 10,
                      help = 'Number of concepts [default=%default]')
    parser.add_option('-e', '--num-entities', dest = 'num_entities', default = 2,
                      help = 'Number of entities [default=%default]')
    parser.add_option('-o', '--output-file', dest = 'outputfile',
                      help = 'Name of output file')
    parser.add_option('-i', '--word-list-file', dest = 'wordlistfile', default = None,
                      help = 'Input word list [default will generate random words]')
    parser.add_option('-u','--number of utterences', dest = 'utterence',default = 1000,
                      help = 'Number of test cases')

    (options, args) = parser.parse_args()

    if DEBUG: print >> sys.stderr, "ENTITY_SIZE_PROB:\n", ENTITY_SIZE_PROB
    if DEBUG: print >> sys.stderr, "CONCEPT_SIZE_PROB:\n", CONCEPT_SIZE_PROB
    if DEBUG: print >> sys.stderr, "ENTITY_GRAMMAR_LENGTH_PROB:\n", ENTITY_GRAMMAR_LENGTH_PROB
    if DEBUG: print >> sys.stderr, "CONCEPT_GRAMMAR_LENGTH_PROB:\n", CONCEPT_GRAMMAR_LENGTH_PROB

    
    if not options.outputfile:
        print >> sys.stderr, "Output file is obligatory"
        parser.print_help()
        sys.exit(1)

    main(options.outputfile, options.num_words, options.num_concepts, options.num_entities, wordlistfile = options.wordlistfile)









