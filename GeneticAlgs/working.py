import numpy as np
from math import sqrt
import random
import matplotlib.pyplot as plt
import os

def run_ga(crossover, mutate, parent_selection, survival):
    population = [Route() for _ in range(POPULATION_SIZE)]
    population.sort(key=lambda x: x.f)

    for generation in range(GENERATIONS):
        parents = parent_selection(population)
        children = []
        for i in range(0,len(parents)-2,2):
            c = crossover(parents[i], parents[i+1])
            if random.random() < MUTATION_CHANCE:
                c = mutate(c)
            children.append(c)

        population = survival(population, children)
        #population.sort(key=lambda x: x.f)
        if(generation % (GENERATIONS // NUM_REPORTS) == 0):
            print(f"Generation {generation}: {population[0].f:.4f}")
    result = population[0]
    print(len(population))
    
    a,b = np.where(result.path[:,0] == INITIAL_CITY[0]), np.where(result.path[:,1] == INITIAL_CITY[1])
    split = int(np.intersect1d(a,b))
    path_ans = np.append(result.path[:split], result.path[split:], axis=0)
    best_answer = Route(path_ans)
    best_answer.path = np.append(result.path, result.path[0].reshape(1,2), axis=0)
    return best_answer

class Route:
    def __init__(self, path=None):
        if path is not None:
            self.path = path #numpy int array of shape (CITIES_COUNT,2)
        else:
            p = CITIES.copy()
            np.random.shuffle(p)
            self.path = p 
        self.f = self.fitness() #float scalar 
        
    def fitness(self):
        shifted = np.append(self.path[1:], self.path[:1], axis=0)
        squared_comp = np.square((np.subtract(self.path,shifted)))
        out = np.sum(np.sqrt(np.sum(squared_comp,axis=1)))
        return out
            
    def __str__(self):
        return f"Route: {self.path}; Fitness: {self.f:.5f}"
    
    def __repr__(self):
        return f"Route at {hex(id(self))}"  #; Fitness: {self.f:.5f}"  

def random_selection(population):
    mating_pool = []
    for i in range(2*OFFSPRING_SIZE):
        mating_pool.append(random.choice(population))
    return mating_pool

#Take a selection from one and fill in before and after
def crossover_ordinary(p1, p2):
    a,b = np.random.randint(0,CITIES_COUNT, size=(2,), dtype='int32')
    start = min(a,b)
    stop = max(a,b)
    if stop - start == CITIES_COUNT-1:
        return p1
    p1_dna = np.array(p1.path[start:stop])
    missing_cities = np.array([city for city in p2.path if not any(np.equal(p1_dna, city).all(1))])
    t = np.append(missing_cities[:start], p1_dna, axis=0)
    return Route(np.append(t, missing_cities[start:], axis=0))

def no_mutation(r):
    return r

def kill_weakest(population, children):
    return sorted(population+children, key=lambda x:x.f)[:POPULATION_SIZE]

def k_elitism(population, children):
    #return population[:ELITISM] + sorted(children, key=lambda x: x.f)[:POPULATION_SIZE-ELITISM]
    return sorted(population[:ELITISM] + children, key=lambda x: x.f)[:POPULATION_SIZE]

# generate random points in a circle
num_samples = 16
radius = 1

# make a simple unit circle 
theta = np.linspace(0, 2*np.pi, num_samples)
a, b = radius * np.cos(theta), radius * np.sin(theta)
c = np.concatenate((a.reshape(num_samples,1), b.reshape(num_samples,1)), axis=1)

np.random.shuffle(c)
CITIES = c
INITIAL_CITY = CITIES[0]
CITIES_COUNT = num_samples
OPTIMAL = 2 * np.pi * radius



POPULATION_SIZE = 200
OFFSPRING_SIZE = POPULATION_SIZE
GENERATIONS = 100
MUTATION_CHANCE = .3
NUM_REPORTS = 100
ELITISM = 2

CROSSOVER_METHOD = crossover_ordinary
#CROSSOVER_METHOD = crossover_old

MUTATE_METHOD = no_mutation 
# MUTATE_METHOD = swap_mutate
# MUTATE_METHOD = flip_mutation

PARENT_SELECTION_METHOD = random_selection

# SURVIVAL_METHOD = kill_weakest
SURVIVAL_METHOD = k_elitism



np.random.seed()
random.seed()
best_answer = run_ga(CROSSOVER_METHOD, MUTATE_METHOD, PARENT_SELECTION_METHOD, SURVIVAL_METHOD)
print(f"Best length: {best_answer.f:.4f}")

#if an optimal length is known 
if OPTIMAL is not None:
    print(f"Optimal Length: {OPTIMAL:.4f}, {100*(OPTIMAL / best_answer.f):.2f} %")